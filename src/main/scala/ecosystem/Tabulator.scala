package ecosystem

// Credits: https://stackoverflow.com/a/7542476
object Tabulator {
  def format(table: List[List[String]]) = table match {
    case List() => ""
    case _ =>
      val sizes = for (row <- table) yield (for (cell <- row) yield if (cell == null) 0 else stripColor(cell.toString).length)
      val colSizes = for (col <- sizes.transpose) yield col.max
      val rows = for (row <- table) yield formatRow(row, colSizes)
      formatRows(rowSeparator(colSizes), rows)
  }

  def formatRows(rowSeparator: String, rows: List[String]): String = (
    rowSeparator ::
    rows.head ::
    rowSeparator ::
    rows.tail.toList :::
    rowSeparator ::
    List()).mkString("\n")

  def formatRow(row: List[String], _colSizes: List[Int]) = {
    val colSizes = (_colSizes.head + 1) :: _colSizes.tail  // head + 1 due to some weird bug where the leading | of a string gets eaten away
    val cells = for ((item, size) <- row.zip(colSizes)) yield if (size == 0) "" else
      item + " " * (size - stripColor(item).size)
    cells.mkString("|", "|", "|")
  }

  def rowSeparator(colSizes: List[Int]) = colSizes map { "-" * _ } mkString("+", "+", "+")
}
