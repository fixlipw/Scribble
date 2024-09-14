import org.junit.jupiter.api.Test;

import com.mxgraph.util.mxResources;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FilesTest {

    @Test
    public void ResourcesTest() {
        assertDoesNotThrow(() -> mxResources.add("src/main/resources"));
    }
}
