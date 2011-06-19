/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian HÃ¶rl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.gsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.php.twig.editor.parsing.TwigParserResult;
import org.openide.filesystems.FileObject;

public class TwigStructureScanner implements StructureScanner {

    @Override
    public List<? extends StructureItem> scan( ParserResult info ) { 
        TwigParserResult result = (TwigParserResult)info;
        List<TwigParserResult.Block> blocks = new ArrayList<TwigParserResult.Block>();
        List<TwigStructureItem> items = new ArrayList<TwigStructureItem>();
        
        for ( TwigParserResult.Block item : result.getBlocks() ) {
            if ( item.getDescription().equals( "block" ) || item.getDescription().equals( "*inline-block" ) ) {
                blocks.add( item );
                System.out.println( item.getDescription() + ": " + item.getOffset() + " to " + ( item.getOffset() + item.getLength() ) );  
            }
        }
        
        boolean isTopLevel = false;
        
        for ( TwigParserResult.Block item : blocks ) {
            
            isTopLevel = true;
            
            for ( TwigParserResult.Block check : blocks ) {
                
                if ( item.getOffset() > check.getOffset() && 
                     item.getOffset() + item.getLength() < check.getOffset() + check.getLength()
                ) {
                    isTopLevel = false;
                    break;
                }
                
            }
            
            if ( isTopLevel ) {
                items.add( new TwigStructureItem( result.getSnapshot(), item, blocks ) );
            }
            
        }

        return items;
        
    }

    @Override
    public Map<String, List<OffsetRange>> folds( ParserResult info ) { 
        
        TwigParserResult result = (TwigParserResult)info;
        Map<String, List<OffsetRange>> folds = new HashMap<String, List<OffsetRange>>();
        
        for ( TwigParserResult.Block block : result.getBlocks() ) {
            
            if ( block.getDescription().startsWith( "*" ) ) continue;
            folds.put( "codeblocks", Collections.singletonList( new OffsetRange( 
                    block.getOffset(), block.getOffset() + block.getLength()
            ) ) );
            
        }
        
        return folds;
        
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration( true, false );
    }
    
}