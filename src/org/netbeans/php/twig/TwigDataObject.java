/**
 * Twig plugin for Netbeans.
 *
 * Copyright (c) 2011 Sebastian Hörl
 *
 * For warranty and licensing information, view the LICENSE file.
 */

package org.netbeans.php.twig;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;

/**
 * Twig MultiDataObject
 *
 * @author Sebastian Hörl
 */
public class TwigDataObject extends MultiDataObject {

    public TwigDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
}
