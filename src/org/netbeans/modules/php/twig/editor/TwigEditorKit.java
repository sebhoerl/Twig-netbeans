package org.netbeans.modules.php.twig.editor;

import org.netbeans.modules.editor.NbEditorKit;

public class TwigEditorKit extends NbEditorKit {
    
    @Override
    public String getContentType() {
        return "text/twig";
    }
    
}
