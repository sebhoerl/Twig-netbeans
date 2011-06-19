/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.embedding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.php.twig.editor.lexer.TwigTopTokenId;

public class TwigEmbeddingProvider extends EmbeddingProvider {

    public static final String GENERATED_CODE = "@@@"; //NOI18N

    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {

        TokenHierarchy<CharSequence> th = TokenHierarchy.create(snapshot.getText(), TwigTopTokenId.language());
        TokenSequence<TwigTopTokenId> sequence = th.tokenSequence(TwigTopTokenId.language());
        if (sequence == null) return Collections.emptyList();

        sequence.moveStart();
        List<Embedding> embeddings = new ArrayList<Embedding>();

        int offset = -1;
        int length = 0;
        while ( sequence.moveNext() ) {
            Token t = sequence.token();
            
            if ( t.id() == TwigTopTokenId.T_HTML ) {
                if ( offset < 0 ) offset = sequence.offset();
                length += t.length();
            } else if ( offset >= 0 ) {
                embeddings.add( snapshot.create( offset, length, "text/html" ) );
                offset = -1;
                length = 0;
            }
        }
        
        if ( offset >= 0 ) {
            embeddings.add( snapshot.create( offset, length, "text/html" ) );
        }
        
        if (embeddings.isEmpty()) {
            return Collections.singletonList( snapshot.create( "", "text/html" ) );
        } else {
            return Collections.singletonList( Embedding.create(embeddings) );
        }
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public void cancel() {}

    public static final class Factory extends TaskFactory {

        @Override
        public Collection<SchedulerTask> create(final Snapshot snapshot) {
            return Collections.<SchedulerTask>singletonList(new TwigEmbeddingProvider());
        }
    }

}