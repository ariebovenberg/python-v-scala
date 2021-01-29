import util.Random
import java.util.{Date}
import scala.collection.{MapView, mutable}

case class Row(x: Float, y: Float, turn: Int, points: Int, date: Date)

object HelloWorld {
  def main(args: Array[String]): Unit = {
    val bufferedSource = io.Source.fromFile(args(0))
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    val rows = (io.Source
      .fromFile(args(0))
      .getLines)
      .drop(1)
      .map(line => {
        val cols = line.split(",").map(_.trim)
        Row(
          cols(9).toFloat,
          cols(10).toFloat,
          cols(6).replace("\"", "").toInt,
          points = cols(8).toInt,
          date = format.parse(cols(2).replace("\"", ""))
        )
      })
    // _plot(rows)
    // _points_per_turn(rows)
    // _points_per_day(rows)
    _10pts_per_month(rows)
    bufferedSource.close
  }

  def _plot(rows: Iterator[Row]): Unit = {
    val rowsAll = rows.toList
    val xs = rowsAll.map(_.x)
    val ys = rowsAll.map(_.y)
    val turns = rowsAll.map(_.turn)
    // couldn't get plotly working in reasonable time, so just imagine plotting
    // Plot().withScatter(xs, ys, color: turns)
  }

  def _merge_only_intersection(
      t1: MapView[Int, Int],
      t2: MapView[Int, Int]
  ): MapView[Int, Int] = {
    t1.iterator
      .foldLeft(List[(Int, Int)]())((merged, pointsInTurn) => {
        val (turn, points1) = pointsInTurn
        t2.get(turn) match {
          case None          => merged
          case Some(points2) => (turn, points1 + points2) :: merged
        }
      })
      .toMap
      .view
  }

  def _points_per_turn(rows: Iterator[Row]): Unit = {
    val totalsPerTurn = (rows.toList
      .groupBy(_.date)
      .mapValues(
        _.groupBy(_.turn).mapValues(_.map(_.points).sum)
      ))
      .valuesIterator
      .reduce(_merge_only_intersection)
      .toList
      .sortBy(_._1)

    println("Turn  Total")
    for ((turn, points) <- totalsPerTurn) {
      println(f"$turn%-5s $points")
    }
  }

  def _points_per_day(rows: Iterator[Row]): Unit = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    val totalsPerDay = rows.toList
      .groupBy(
        _.date
      )
      .mapValues(_.map(_.points).sum)
      .toList
      .sortBy(_._1)
    println("Day  Total")
    for ((day, points) <- totalsPerDay) {
      println(f"${format.format(day)} $points")
    }
  }

  def _10pts_per_month(rows: Iterator[Row]): Unit = {
    val countPerMonth =
      rows.toList.groupBy(_.date.getMonth()).mapValues(_.count(_.points == 10)).toList.sortBy(_._1)
    println("Month  Count")
    for ((month, points) <- countPerMonth) {
      println(f"$month%-6s $points")
    }
  }

}
