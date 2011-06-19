/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.twig.editor.gsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.php.twig.editor.parsing.TwigParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 * @author sebastian
 */
public class TwigStructureItem implements StructureItem {

    List<TwigStructureItem> blocks;
    TwigParserResult.Block item;
    Snapshot snapshot;
    
    public TwigStructureItem( Snapshot snapshot, TwigParserResult.Block item, List<TwigParserResult.Block> blocks ) {
        
        this.item = item;
        this.blocks = new ArrayList<TwigStructureItem>();
        this.snapshot = snapshot;
        
        for ( TwigParserResult.Block current : blocks ) {

            if ( 
                item.getOffset() < current.getOffset() && 
                current.getOffset() + current.getLength() < item.getOffset() + item.getLength() 
            ) {

                this.blocks.add( new TwigStructureItem( snapshot, current, blocks ) );

            }

        }
        
    }
    
    @Override
    public String getName() {
        return "Block " + item.getExtra();
    }

    @Override
    public String getSortText() {
        return "Block " + item.getDescription();
    }

    @Override
    public String getHtml(HtmlFormatter hf) {
        return "Block " + item.getExtra();
    }

    @Override
    public ElementHandle getElementHandle() {
        return new TwigElementHandle( item, snapshot );
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.ATTRIBUTE;
    }

    @Override
    public Set<Modifier> getModifiers() {
        if ( CharSequenceUtilities.startsWith( item.getDescription(), "*" ) )
            return Collections.singleton( Modifier.STATIC );
        return Collections.emptySet();
    }

    @Override
    public boolean isLeaf() {
        return blocks.isEmpty();
    }

    @Override
    public List<? extends StructureItem> getNestedItems() {
        return blocks;
    }

    @Override
    public long getPosition() {
        return item.getOffset();
    }

    @Override
    public long getEndPosition() {
        return item.getOffset() + item.getLength();
    }

    @Override
    public ImageIcon getCustomIcon() {
        return null;
    }
    
    class TwigElementHandle implements ElementHandle {

        TwigParserResult.Block item;
        Snapshot snapshot;
        
        public TwigElementHandle( TwigParserResult.Block item, Snapshot snapshot ) {
            this.item = item;
            this.snapshot = snapshot;
        }
        
        @Override
        public FileObject getFileObject() {
            return snapshot.getSource().getFileObject();
        }

        @Override
        public String getMimeType() {
            return "text/twig";
        }

        @Override
        public String getName() {
            return "Block " + item.getExtra();
        }

        @Override
        public String getIn() {
            return "Block " + item.getExtra();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.ATTRIBUTE;
        }

        @Override
        public Set<Modifier> getModifiers() {
            if ( CharSequenceUtilities.startsWith( item.getDescription(), "*" ) )
                return Collections.singleton( Modifier.STATIC );
            return Collections.emptySet();
        }

        @Override
        public boolean signatureEquals(ElementHandle eh) {
            if ( !(eh instanceof TwigElementHandle) ) return false;
            if ( eh.getName().equals(this.getName()) ) return true;
            return false;
        }

        @Override
        public OffsetRange getOffsetRange( ParserResult pr ) {
            return new OffsetRange( item.getOffset(), item.getOffset() + item.getLength() );
        }
        
        
    }
    
}
