package io.microscape.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    private String headline;
    private String text;

    @Builder.Default
    private List<Section> content = new ArrayList<>(10);
}
