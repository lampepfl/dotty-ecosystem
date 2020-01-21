package ecosystem.rendering

// Credits: https://stackoverflow.com/a/7542476
object Tabulator {
  def format(data: List[List[String]]) = data match
    case List() => ""
    case header :: body =>
      val table = header.map(bold) :: body
      val sizes = for (row <- table) yield (for (cell <- row) yield if (cell == null) 0 else stripDecorations(cell.toString).length)
      val colSizes = for (col <- sizes.transpose) yield col.max
      val rows = for (row <- table) yield formatRow(row, colSizes)
      formatRows(rowSeparator(colSizes), rows)

  def formatRows(rowSeparator: String, rows: List[String]): String = (
    rowSeparator ::
    rows.head ::
    rowSeparator ::
    rows.tail.toList :::
    rowSeparator ::
    List()).mkString("\n")

  def formatRow(row: List[String], colSizes: List[Int]) = {
    val cells = for ((item, size) <- row.zip(colSizes)) yield if (size == 0) "" else
      item + " " * (size - stripDecorations(item).size)
    cells.mkString("|", "|", "|")
  }

  def rowSeparator(colSizes: List[Int]) = colSizes map { "-" * _ } mkString("+", "+", "+")
}
