/*******************************************************************************
 * Copyright 2019 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package grondag.fermion.varia;

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
