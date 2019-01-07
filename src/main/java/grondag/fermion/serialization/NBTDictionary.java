package grondag.fermion.serialization;

import java.util.HashSet;

/**
 * Maintains a list of NBT tags that have been claimed to prevent duplication.
 * Purpose is to prevent me from being a dumbass and using the same tag for
 * different data. If a duplicate is found, raises an assertion error but allows
 * the tag.
 */
public class NBTDictionary {
    private static final HashSet<String> tagNames = new HashSet<>();

    /**
     * Returns tag name that is passed in, raising an assertion error if was used
     * before. Future version may attempt to deduplicate so retain result instead of
     * paramter.
     */
    public static String claim(String tagName) {
        assert !tagNames.contains(tagName) : "Duplicate NBT Tag name";
        tagNames.add(tagName);
        return tagName;
    }

}
