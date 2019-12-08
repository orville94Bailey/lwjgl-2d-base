package bulk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    public void TestActivations(){
        Tile holder = new Tile(true);
        assert holder.getActivations() == 0;
        holder.Activate();
        assert holder.getActivations() == 1;
    }
}