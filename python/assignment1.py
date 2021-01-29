import csv
from collections import defaultdict
from dataclasses import dataclass
from datetime import date, datetime
from operator import attrgetter
from typing import IO, Iterable

import click
import plotly.express as px

DATE_FMT = "%Y-%m-%d"

Turn = int
Points = int


@dataclass(frozen=True)
class Row:
    x: float
    y: float
    turn: Turn
    points: Points
    date: date


@click.command()
@click.argument("targets", type=click.File())
def run(targets: IO[str]) -> None:
    next(targets)  # skip header row
    rows = [
        Row(
            x=float(row[9]),
            y=float(row[10]),
            turn=int(row[6]),
            points=int(row[8]),
            date=datetime.strptime(row[2], DATE_FMT).date(),
        )
        for row in csv.reader(targets)
    ]
    # _plot(rows)
    _print_turn_points(rows)
    _print_date_points(rows)
    _print_10pt_arrows_per_month(rows)


def _plot(rows: Iterable[Row]) -> None:
    xs, ys, turns = zip(*map(attrgetter("x", "y", "turn"), rows))
    fig = px.scatter(x=xs, y=ys, color=turns)
    fig.show()


def _print_turn_points(rows: Iterable[Row]) -> None:
    # seems like there should be a simpler way. Anyway...this also works
    points_per_turn = defaultdict(int)
    dates_per_turn = defaultdict(set)

    for r in rows:
        points_per_turn[r.turn] += r.points
        dates_per_turn[r.turn].add(r.date)

    n_dates = max(len(ds) for ds in dates_per_turn.values())
    for turn, dates in dates_per_turn.items():
        if len(dates) < n_dates:
            points_per_turn.pop(turn)

    print("Turn  Total points")
    for turn, points in sorted(points_per_turn.items()):
        print(f"{turn:<4}  {points}")


def _print_date_points(rows: Iterable[Row]) -> None:
    points_per_date = defaultdict(int)
    for r in rows:
        points_per_date[r.date] += r.points

    print("Date        Total points")
    for date_, points in sorted(points_per_date.items()):
        print(f"{date_}  {points}")


def _print_10pt_arrows_per_month(rows: Iterable[Row]) -> None:
    result = defaultdict(int)
    for r in rows:
        if r.points == 10:
            result[r.date.month] += 1

    print("Month Count")
    for month, count in sorted(result.items()):
        print(f"{month:<4}  {count}")


if __name__ == "__main__":
    run()
