package org.netbeans.php.twig;
import org.netbeans.php.twig.lexer.*;
import java.util.*;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.api.lexer.*;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;

/**
 * Twig Embedding
 *
 * @author Sebastian Hörl
 */
public class TwigEmbeddingProvider extends EmbeddingProvider {

    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {

        TokenHierarchy<CharSequence> th = TokenHierarchy.create( snapshot.getText(), NbTwigTokenId.getLanguage() );
        TokenSequence<NbTwigTokenId> sequence = th.tokenSequence( NbTwigTokenId.getLanguage() );

        if ( sequence == null ) return Collections.emptyList();

        sequence.moveStart();
        List<Embedding> embeddings = new ArrayList<Embedding>();

        int from = -1;
        int length = 0;

        while ( sequence.moveNext() ) {
            NbTwigTokenId t = (NbTwigTokenId) sequence.token().id();

            if ( t.type == TwigToken.Type.TEXT ) {

                if ( from < 0 ) {
                    from = sequence.offset();
                }

                length += sequence.token().length();

            } else {

                if ( from >= 0 ) {

                    embeddings.add( snapshot.create( from, length, "text/html" ) );

                }

                from = -1;
                length = 0;

            }

        }

        if ( from >= 0 ) {

            embeddings.add( snapshot.create( from, length, "text/html" ) );

        }

        return Collections.singletonList( Embedding.create( embeddings ) );

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

            return Collections.<SchedulerTask> singletonList( new TwigEmbeddingProvider() );
            
        }

    }

}
