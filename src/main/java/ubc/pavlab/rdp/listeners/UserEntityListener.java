package ubc.pavlab.rdp.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ubc.pavlab.rdp.model.*;
import ubc.pavlab.rdp.services.GOService;
import ubc.pavlab.rdp.services.GeneService;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;

/**
 * Created by mjacobson on 29/01/18.
 */
@Component
public class UserEntityListener {

    private static Log log = LogFactory.getLog( UserEntityListener.class );

    static private GeneService geneService;
    static private GOService goService;

    @PostConstruct
    public void init() {
        log.info( "Initializing with dependency [" + geneService + ", " + goService + "]" );
    }

    @Autowired(required = true)
    @Qualifier("geneService")
    public void setGeneService(GeneService geneService)
    {
        UserEntityListener.geneService = geneService;
    }

    @Autowired(required = true)
    @Qualifier("goService")
    public void setGOService(GOService goService)
    {
        UserEntityListener.goService = goService;
    }

    @PostLoad
    void onPostLoad( User user ) {
        log.info( "onPostLoad: " + user );

        if ( geneService == null || goService == null ) {
            log.error( "Services not autowiring in User Entity Listener!" );
            return;
        }

        for ( UserGene userGene : user.getUserGenes() ) {
            Gene gene = userGene.getGene();
            Gene cachedGene = geneService.load( gene.getId() );

            if ( cachedGene != null ) {
                userGene.setGene( cachedGene );
            }
        }

        for ( UserTerm userTerm : user.getUserTerms() ) {
            GeneOntologyTerm term = userTerm.getTerm();
            GeneOntologyTerm cachedTerm = goService.getTerm( term.getId() );

            if ( cachedTerm != null ) {
                userTerm.setTerm( cachedTerm );
            }
        }

        // We don't save the user, we leave that up to the user.
    }


}
