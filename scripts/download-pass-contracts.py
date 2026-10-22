#!/usr/bin/env python3
"""Download the full DC Open Data "Contracts from PASS" dataset as one CSV.

Source (the only public locator this script needs):
  https://opendata.dc.gov/datasets/dc4071b2f5fc47519ead7bc3e90ff180_37/api

Usage:
  python3 scripts/download-pass-contracts.py
  python3 scripts/download-pass-contracts.py -o /tmp/PASS_Contracts.csv
  python3 scripts/download-pass-contracts.py --via query
"""

from pass_opendata import CONTRACTS, run

if __name__ == "__main__":
    raise SystemExit(run(CONTRACTS))
