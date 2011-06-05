/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.gsf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

public class TwigStructureScanner implements StructureScanner {

    @Override
    public List<? extends StructureItem> scan(ParserResult info) { return Collections.emptyList(); }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult info) { return Collections.emptyMap(); }

    @Override
    public Configuration getConfiguration() { return null; }
    
}