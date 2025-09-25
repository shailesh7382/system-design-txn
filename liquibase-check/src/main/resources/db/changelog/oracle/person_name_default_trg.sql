DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM user_triggers WHERE trigger_name = 'PERSON_NAME_DEFAULT_TRG';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE TRIGGER person_name_default_trg
            BEFORE INSERT ON person
            FOR EACH ROW
            BEGIN
                IF :NEW.name IS NULL THEN
                    :NEW.name := ''Unknown'';
                END IF;
            END;';
    END IF;
END;
/

