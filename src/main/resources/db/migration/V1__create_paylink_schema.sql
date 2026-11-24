IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'paylink')
BEGIN
    EXEC('CREATE SCHEMA paylink');
END;
