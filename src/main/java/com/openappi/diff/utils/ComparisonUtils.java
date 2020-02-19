package com.openappi.diff.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ComparisonUtils {

	public static Map<String, Collection<String>> findDiff(Collection<String> oldTags, Collection<String> newTags) {

		Collection<String> removed = oldTags.stream().filter(aObject -> {
			return !newTags.contains(aObject);
		}).collect(Collectors.toList());

		Collection<String> added = newTags.stream().filter(aObject -> !oldTags.contains(aObject))
				.collect(Collectors.toList());

		HashMap<String, Collection<String>> tagDiff = new HashMap<>();
		tagDiff.put("removed", removed);
		tagDiff.put("added", added);

		return tagDiff;
	}

	public static Boolean isDiff(String s1, String s2) {
		if (s1 == s2)
			return false;
		
		if(Objects.nonNull(s1) && Objects.isNull(s2)) return true;
		if(Objects.nonNull(s2) && Objects.isNull(s1)) return true;
		
		return !s1.equals(s2);
	}
}
