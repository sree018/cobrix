package za.co.absa.cobrix.cobol.parser.encoding.codepage

/**
  * EBCDIC code page with support for Thai script used in IBM mainframes
  */
class CodePage838 extends CodePage {
  override def codePageShortName: String = "cp838"

  override protected def ebcdicToAsciiMapping: Array[Char] = {
    /* This is the EBCDIC Code Page 838 to ASCII conversion table with non-printable characters mapping
       from https://en.everybodywiki.com/EBCDIC_838 */
    val ebcdic2ascii: Array[Char] = {
      val clf = '\r'
      val ccr = '\n'
      val spc = ' '
      val qts = '\''
      val qtd = '\"'
      val bsh = '\\'
      val code01 = '\u0E48'
      val code02 = '\u0E4E'
      val code03 = '\u0E31'
      val code04 = '\u0E34'
      val code05 = '\u0E49'
      val code06 = '\u0E35'
      val code07 = '\u0E36'
      val code08 = '\u0E37'
      val code09 = '\u0E38'
      val code10 = '\u0E39'
      val code11 = '\u0E3A'
      val code12 = '\u0E47'
      val code13 = '\u0E48'
      val code14 = '\u0E49'
      val code15 = '\u0E4A'
      val code16 = '\u0E4B'
      val code17 = '\u0E4C'
      val code18 = '\u0E4D'

      Array[Char](
        spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, ccr, spc, spc, //   0 -  15
        spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, //  16 -  31
        spc, spc, spc, spc, spc, clf, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, //  32 -  47
        spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, spc, //  48 -  63
        spc, spc, 'ก', 'ข', 'ฃ', 'ค', 'ฅ', 'ฆ', 'ง', '[', '¢', '.', '<', '(', '+', '|', //  64 -  79
        '&', code01, 'จ', 'ฉ', 'ช', 'ซ', 'ฌ', 'ญ', 'ฎ', ']', '!', '$', '*', ')', ';', '¬', //  80 -  95
        '-', '/', 'ฏ', 'ฐ', 'ฑ', 'ฒ', 'ณ', 'ด', 'ต', '^', '¦', ',', '%', '_', '>', '?', //  96 - 111
        '฿', code02, 'ถ', 'ท', 'ธ', 'น', 'บ', 'ป', 'ผ', '`', ':', '#', '@', qts, '=', qtd, // 112 - 127
        '๏', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'ฝ', 'พ', 'ฟ', 'ภ', 'ม', 'ย', // 128 - 143
        '๚', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 'ร', 'ฤ', 'ล', 'ฦ', 'ว', 'ศ', // 144 - 159
        '๛', '~', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ษ', 'ส', 'ห', 'ฬ', 'อ', 'ฮ', // 160 - 175
        '๐', '๑', '๒', '๓', '๔', '๕', '๖', '๗', '๘', '๙', 'ฯ', 'ะ', code03, 'า', 'ำ', code04, // 176 - 191
        '{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', code05, code06, code07, code08, code09, code10, // 192 - 207
        '}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', code11, 'เ', 'แ', 'โ', 'ใ', 'ไ', // 208 - 223
        bsh, code15, 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'ๅ', 'ๆ', code12, code13, code14, code15, // 224 - 239
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', code16, code17, code18, code16, code17, spc) // 240 - 255
    }
    ebcdic2ascii
  }

}
