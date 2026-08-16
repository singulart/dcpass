CREATE VIEW pass_contract_dcss AS
SELECT *
FROM pass_contract con
WHERE pass_contract_fts_match(con.search_vector, 'DCSS')
   OR con.procurementmethoddescription = 'DC Supply Schedule';