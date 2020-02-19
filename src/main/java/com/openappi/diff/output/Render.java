package com.openappi.diff.output;

import com.openappi.diff.model.ChangedOpenAPI;

public interface Render {

	String render(ChangedOpenAPI diff);

}
