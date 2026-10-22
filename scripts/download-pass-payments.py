#!/usr/bin/env python3
"""Download the full DC Open Data "Payments from PASS" dataset as one CSV.

Source (the only public locator this script needs):
  https://opendata.dc.gov/datasets/3ab69ad2cbf049b1851f14cb2fcdd440_17/api

Usage:
  python3 scripts/download-pass-payments.py
  python3 scripts/download-pass-payments.py -o /tmp/PASS_Payments.csv
  python3 scripts/download-pass-payments.py --via query
"""

from pass_opendata import PAYMENTS, run

if __name__ == "__main__":
    raise SystemExit(run(PAYMENTS))
