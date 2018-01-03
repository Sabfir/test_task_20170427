package integration.util;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

public class IgnoreIdComparator extends DefaultComparator {
    public IgnoreIdComparator(JSONCompareMode mode) {
        super(mode);
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult jsonCompareResult) throws JSONException {

        if (prefix.endsWith("id")) {
            // Return - don't apply default value checking
            return;
        }

        super.compareValues(prefix, expectedValue, actualValue, jsonCompareResult);
    }
}
