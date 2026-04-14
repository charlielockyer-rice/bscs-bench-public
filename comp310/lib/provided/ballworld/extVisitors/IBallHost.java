package provided.ballworld.extVisitors;

import provided.extvisitor.IExtVisitorHost;

/**
 * Type-narrowed extended visitor host for balls. 
 * This interface needs further type-narrowing to be used in student code!
 * @author swong
 *
 * @param <H>   The specific ball sub-host type
 */
public interface IBallHost<H extends IBallHost<? extends H>> extends IExtVisitorHost<IBallHostID, H> {

}
