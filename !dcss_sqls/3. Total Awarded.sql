-- Total Awarded DCSS Dollars (since the beginning of time).
SELECT COALESCE(SUM(pototal), 0) AS total_task_order_dollars
FROM purchase_order_dcss;
