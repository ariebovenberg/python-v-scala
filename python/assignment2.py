from pathlib import Path

import requests
from bs4 import BeautifulSoup

URL = "https://httpstatusdogs.com/"

result_dir = Path("scraping-results")
result_dir.mkdir(exist_ok=True)


def process_img(elem):
    img_elem = elem.find("img")
    url = URL + img_elem.attrs["src"]
    name = img_elem["alt"]
    (result_dir / name).with_suffix(".jpg").write_bytes(
        requests.get(url).content
    )


def run():
    page_str = requests.get(URL).text
    soup = BeautifulSoup(page_str, "lxml")
    for elem in soup.findAll("a", {"class": "thumbnail"}):
        process_img(elem)


if __name__ == "__main__":
    run()
