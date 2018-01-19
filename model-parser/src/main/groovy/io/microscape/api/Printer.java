package io.microscape.api;

import io.microscape.annotations.OutputPlugin;
import io.microscape.api.Documentation;

@OutputPlugin
public interface Printer {

    String print(Documentation documentation);

}
