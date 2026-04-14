"""
Provided code for Sports Analytics.
"""

def print_weights(model):
    """
    Print out all of the weights each with the name of the associated
    statistic.
    """
    weights = model.get_weights()
    num_stats = len(STATS)
    shape = weights.shape()
    if (num_stats, 1) != shape:
        errstr = ("model must be a matrix of weights with shape (%d, 1), received shape %s" %
                 (num_stats, str(shape)))
        raise ValueError(errstr)
    for i in range(num_stats):
        rounded = round(weights[i, 0], 3)
        print("% 0.3f %3d %-3s %s" % (rounded, i, STATS[i][0], STATS[i][1]))

def extract_team_wins(wins, team, year):
    """
    Find and return the wins for the given team/year from the provided
    matrix.
    Note that this ONLY works for the 2001-2012 test data.
    """
    if (330, 1) != wins.shape():
        raise ValueError("extract_team_wins must be called with 2001-2012 data")
    if year <= 2001 or year > 2012:
        raise ValueError("year must be between 2002 and 2012")
    year_index = year - 2002
    team_index = TEAMS.index(team)
    if team_index == -1:
        raise ValueError("unable to find team " + str(team))
    index = team_index * 11 + year_index
    return wins[index, 0]

TEAMS = ['ANA', 'ARI', 'ATL', 'BAL', 'BOS', 'CHA', 'CHN', 'CIN',
         'CLE', 'COL', 'DET', 'FLO', 'HOU', 'KCA', 'LAN', 'MIL',
         'MIN', 'NYA', 'NYN', 'OAK', 'PHI', 'PIT', 'SDN', 'SEA',
         'SFN', 'SLN', 'TBA', 'TEX', 'TOR', 'WAS']

STATS = [("WON", "Games won this year"),
         ("AB", "At bat"),
         ("R", "Runs"),
         ("H", "Hits"),
         ("2B", "Double"),
         ("3B", "Triple"),
         ("HR", "Home Runs"),
         ("RBI", "Runs batted in"),
         ("BB", "Base on balls (walk)"),
         ("IBB", "Intentional base on balls"),
         ("SO", "Strike Out"),
         ("HBP", "Hits by pitch"),
         ("SH", "Sacrifice hits"),
         ("SF", "Sacrifice flies"),
         ("XI", "Extra Inning"),
         ("ROE", "Runs on error"),
         ("GDP", "Ground double play"),
         ("SB", "Stolen bases"),
         ("CS", "Caught stealing"),
         ("H", "Hits"),
         ("BFP", "Batters Facing Pitcher"),
         ("HR", "Home Runs"),
         ("R", "Runs"),
         ("ER", "Earned Runs"),
         ("BB", "Bases on Balls (Walks)"),
         ("IB", "Intentional Walks"),
         ("SO", "Strike Outs"),
         ("SH", "Sacrifice Hits  "),
         ("SF", "Sacrifice Flies"),
         ("WP", "Wild Pitches"),
         ("HBP", "Hit Battters"),
         ("BK", "Balks"),
         ("2B", "Doubles"),
         ("3B", "Triples"),
         ("GDP", "Ground Double Play"),
         ("ROE", "Runs on Error"),
         ("AB", "At bat"),
         ("R", "Runs"),
         ("H", "Hits"),
         ("2B", "Double"),
         ("3B", "Triple"),
         ("HR", "Home Runs"),
         ("RBI", "Runs batted in"),
         ("BB", "Base on balls (walk)"),
         ("IBB", "Intentional base on balls"),
         ("SO", "Strike Out "),
         ("HBP", "Hit by pitch"),
         ("SH", "Sacrifice hits"),
         ("SF", "Sacrifice flies"),
         ("XI", "Extra Inning"),
         ("ROE", "Runs on error"),
         ("GDP", "Ground double play"),
         ("SB", "Stolen bases"),
         ("CS", "Caught stealing"),
         ("H", "Hits"),
         ("BFP", "Batters Facing Pitcher"),
         ("HR", "Home Runs"),
         ("R", "Runs"),
         ("ER", "Earned Runs"),
         ("BB", "Bases on Balls (Walks)"),
         ("IB", "Intentional Walks"),
         ("SO", "Strike Outs"),
         ("SH", "Sacrifice Hits"),
         ("SF", "Sacrifice Flies"),
         ("WP", "Wild Pitches"),
         ("HBP", "Hit Battters"),
         ("BK", "Balks"),
         ("2B", "Doubles"),
         ("3B", "Triples"),
         ("GDP", "Ground Double Play"),
         ("ROE", "Runs on Error"),
         ("AB", "At bat"),
         ("R", "Runs"),
         ("H", "Hits"),
         ("2B", "Double"),
         ("3B", "Triple"),
         ("HR", "Home Runs"),
         ("RBI", "Runs batted in"),
         ("BB", "Base on balls (walk)"),
         ("IBB", "Intentional base on balls"),
         ("SO", "Strike Out"),
         ("HBP", "Hit by pitch"),
         ("SH", "Sacrifice hits"),
         ("SF", "Sacrifice flies"),
         ("XI", "Extra Innings"),
         ("ROE", "Runs on error"),
         ("GDP", "Ground double play"),
         ("SB", "Stolen Bases"),
         ("CS", "Caught stealing"),
         ("H", "Hits"),
         ("BFP", "Batters Facing Pitcher"),
         ("HR", "Home Runs"),
         ("R", "Runs"),
         ("ER", "Earned Runs"),
         ("BB", "Bases on Balls (Walks)"),
         ("IB", "Intentional Walks"),
         ("SO", "Strike Outs"),
         ("SH", "Sacrifice Hits"),
         ("SF", "Sacrifice Flies"),
         ("WP", "Wild Pitches"),
         ("HBP", "Hit Battters"),
         ("BK", "Balks"),
         ("2B", "Doubles"),
         ("3B", "Triples"),
         ("GDP", "Ground Double Play"),
         ("ROE", "Runs on Error"),
         ("PO", "Put outs"),
         ("A", "Assists"),
         ("ERR", "Error"),
         ("DP", "Double Play"),
         ("TP", "Triple Play"),
         ("PO", "Put outs"),
         ("A", "Assists"),
         ("ERR", "Error"),
         ("DP", "Double Play"),
         ("TP", "Triple Play"),
         ("PO", "Put outs"),
         ("A", "Assists"),
         ("ERR", "Error"),
         ("DP", "Double Play"),
         ("TP", "Triple Play"),
         ("CON", "Constant Factor")]
