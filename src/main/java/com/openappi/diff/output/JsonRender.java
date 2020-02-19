package com.openappi.diff.output;

import com.alibaba.fastjson.JSON;
import com.openappi.diff.model.ChangedOpenAPI;

public class JsonRender implements Render {

	@Override
	public String render(ChangedOpenAPI diff) {
		return JSON.toJSONString(diff);
	}
}
