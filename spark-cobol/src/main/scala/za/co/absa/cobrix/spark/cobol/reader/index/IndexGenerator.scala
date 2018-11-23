/*
 * Copyright 2018 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.cobrix.spark.cobol.reader.index

import za.co.absa.cobrix.cobol.parser.Copybook
import za.co.absa.cobrix.cobol.parser.ast.Primitive
import za.co.absa.cobrix.cobol.parser.common.{BinaryUtils, DataExtractors}
import za.co.absa.cobrix.cobol.parser.stream.SimpleStream
import za.co.absa.cobrix.spark.cobol.reader.Constants
import za.co.absa.cobrix.spark.cobol.reader.index.entry.SimpleIndexEntry

import scala.collection.mutable.ArrayBuffer

object IndexGenerator {
  private val xcomHeaderBlock = 4

  def simpleIndexGenerator(fileId: Int,
                           dataStream: SimpleStream,
                           recordsPerIndexEntry: Option[Int] = None,
                           sizePerIndexEntryMB: Option[Int] = None,
                           copybook: Option[Copybook] = None,
                           segmentField: Option[Primitive] = None): ArrayBuffer[SimpleIndexEntry] = {
    var byteIndex = 0L
    val index = new ArrayBuffer[SimpleIndexEntry]
    var rootRecordId: String = ""
    var rootRecordSize = 0
    var recordsInChunk = 0
    var bytesInChunk = 0L
    var recordIndex = 0
    var isHierarchical = copybook.nonEmpty && segmentField.nonEmpty

    val needSplit = getSplitCondition(recordsPerIndexEntry, sizePerIndexEntryMB)

    var endOfFileReached = false
    while (!endOfFileReached) {
      val recordSize = getNextRecordSize(dataStream)
      if (recordSize <= 0) {
        endOfFileReached = true
      } else {
        val record = dataStream.next(recordSize)
        if (record.length < recordSize) {
          endOfFileReached = true
        } else {
          if (isHierarchical && recordIndex == 0) {
            rootRecordSize = recordSize
            rootRecordId = getSegmentId(copybook.get, segmentField.get, record)
            if (rootRecordId.isEmpty) {
              throw new IllegalStateException(s"Root record segment id cannot be empty at $byteIndex.")
            }
          }
          if (recordIndex == 0 || needSplit(recordsInChunk, bytesInChunk)) {
            if (!isHierarchical || isSegmentGoodForSplit(rootRecordSize, rootRecordId, copybook.get, segmentField.get, record)) {
              val indexEntry = SimpleIndexEntry(byteIndex, -1, fileId, recordIndex)
              val len = index.length
              if (len > 0) {
                index(len - 1) = index(len - 1).copy(offsetTo = indexEntry.offsetFrom)
              }
              index += indexEntry
              recordsInChunk = 0
              bytesInChunk = 0L
            }
          }
        }
      }
      byteIndex += xcomHeaderBlock + recordSize
      recordIndex += 1
      recordsInChunk += 1
      bytesInChunk += xcomHeaderBlock + recordSize
    }

    index
  }

  /** Returns a predicate that returns true when current index entry has reached the required size */
  private def getSplitCondition(recordsPerIndexEntry: Option[Int], sizePerIndexEntryMB: Option[Int]) = {
    val bytesPerIndexEntry = sizePerIndexEntryMB.getOrElse(Constants.defaultIndexEntrySizeMB).toLong * Constants.megabyte
    val recPerIndexEntry = recordsPerIndexEntry.getOrElse(1)

    if (recordsPerIndexEntry.isDefined) {
      (records: Int, currentSize: Long) => {
        records >= recPerIndexEntry
      }
    } else {
      (records: Int, currentSize: Long) => {
        currentSize >= bytesPerIndexEntry
      }
    }
  }

  private def isSegmentGoodForSplit(rootRecordSize: Int,
                                    rootRecordId: String,
                                    copybook: Copybook,
                                    segmentField: Primitive,
                                    record: Array[Byte]): Boolean = {
    if (record.length != rootRecordSize) {
      false
    } else {
      rootRecordId == getSegmentId(copybook, segmentField, record)
    }
  }

  private def getNextRecordSize(dataStream: SimpleStream): Int = BinaryUtils.extractXcomRecordSize(dataStream.next(xcomHeaderBlock))

  private def getSegmentId(copybook: Copybook, segmentIdField: Primitive, data: Array[Byte]): String = {
    copybook.extractPrimitiveField(segmentIdField, data).toString.trim
  }
}
