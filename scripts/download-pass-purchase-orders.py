#!/usr/bin/env python3
"""Download the full DC Open Data "Purchase Orders from PASS" dataset as one CSV.

Source (the only public locator this script needs):
  https://opendata.dc.gov/datasets/28fffe702af44277956763e50c35f577_16/api

Usage:
  python3 scripts/download-pass-purchase-orders.py
  python3 scripts/download-pass-purchase-orders.py -o /tmp/PASS_Purchase_Orders.csv
  python3 scripts/download-pass-purchase-orders.py --via query
"""

from pass_opendata import PURCHASE_ORDERS, run

if __name__ == "__main__":
    raise SystemExit(run(PURCHASE_ORDERS))
