'''
Created on Oct 20, 2016
@author: Rohan Achar
'''
from rtypes.pcc.attributes import dimension, primarykey, predicate
from rtypes.pcc.types.subset import subset
from rtypes.pcc.types.set import pcc_set
from rtypes.pcc.types.projection import projection
from rtypes.pcc.types.impure import impure
from datamodel.search.server_datamodel import Link, ServerCopy

@pcc_set
class Wtrinh2Antl2Huyqn6Link(Link):
    USERAGENTSTRING = "Wtrinh2Antl2Huyqn6"

    @dimension(str)
    def user_agent_string(self):
        return self.USERAGENTSTRING

    @user_agent_string.setter
    def user_agent_string(self, v):
        # TODO (rachar): Make it such that some dimensions do not need setters.
        pass


@subset(Wtrinh2Antl2Huyqn6Link)
class Wtrinh2Antl2Huyqn6UnprocessedLink(object):
    @predicate(Wtrinh2Antl2Huyqn6Link.download_complete, Wtrinh2Antl2Huyqn6Link.error_reason)
    def __predicate__(download_complete, error_reason):
        return not (download_complete or error_reason)


@impure
@subset(Wtrinh2Antl2Huyqn6UnprocessedLink)
class OneWtrinh2Antl2Huyqn6UnProcessedLink(Wtrinh2Antl2Huyqn6Link):
    __limit__ = 1

    @predicate(Wtrinh2Antl2Huyqn6Link.download_complete, Wtrinh2Antl2Huyqn6Link.error_reason)
    def __predicate__(download_complete, error_reason):
        return not (download_complete or error_reason)

@projection(Wtrinh2Antl2Huyqn6Link, Wtrinh2Antl2Huyqn6Link.url, Wtrinh2Antl2Huyqn6Link.download_complete)
class Wtrinh2Antl2Huyqn6ProjectionLink(object):
    pass
