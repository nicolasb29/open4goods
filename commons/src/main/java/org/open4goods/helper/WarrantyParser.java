package org.open4goods.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.open4goods.exceptions.InvalidParameterException;
import org.open4goods.exceptions.ResourceNotFoundException;

public class WarrantyParser {

	public static Integer parse(final String val) throws InvalidParameterException {

		String tmp = val.toLowerCase();

		tmp = tmp.replace("*", "");
		tmp = tmp.replace("garantie", "");
		tmp = StringUtils.normalizeSpace(tmp);

		switch (tmp) {
		case "1 an":
			return 12;
		case "2 ans":
			return 24;
		case "3 ans":
			return 36;
		case "4 ans":
			return 48;
		case "5 ans":
			return 60;
		case "6 ans":
			return 72;
		}


		if (!NumberUtils.isNumber(tmp)) {
			throw new ResourceNotFoundException("Not a numeric parsable Warranty value : " + tmp);
		}

		// Default numeric is year
		//TODO(feature, P1, 0.5) : Redesign through regexp
		return NumberUtils.createInteger(tmp)*12;

	}
}
