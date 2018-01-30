package io.microscape.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Documentation {

    @Builder.Default
    private String id = StringUtils.EMPTY;

    @Builder.Default
    private List<Section> sections = new ArrayList<>(10);
}
