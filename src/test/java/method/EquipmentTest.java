package method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EquipmentTest {
    @Test
    void archive_activeEquipment_becomesArchived(){
        Equipment e=new Equipment("EQ-A001", "SIM-P100", "ACTIVE");

        e.archive();

        assertEquals("ARCHIVED",e.getState());
    }

    @Test
    void archive_archivedEquipment_throws(){

        Equipment e=new Equipment("EQ-A002", "SIM-P100", "ARCHIVED");

        assertThrows(IllegalStateException.class, e::archive);
    }

}
