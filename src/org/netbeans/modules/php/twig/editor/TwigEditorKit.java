package org.netbeans.modules.php.twig.editor;

import java.io.IOException;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.spi.editor.fold.FoldManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

public class TwigEditorKit extends NbEditorKit {
    
    @Override
    public Document createDefaultDocument() {

        return super.createDefaultDocument();
        
    }
    
    @Override
    public String getContentType() {
        return "text/twig";
    }
    
}
