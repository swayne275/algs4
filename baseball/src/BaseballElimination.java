/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final int numTeams;
    private final int[] wins;
    private final int[] losses;
    private final int[] gamesRemaining;
    private final String[] teamNames;
    private final int[][] games;
    private final ST<String, Integer> nameIndexLookup;
    private boolean temporaryIsEliminated = false;
    private SET<String> temporaryCertificateOfElimination;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In input = new In(filename);
        numTeams = Integer.parseInt(input.readLine());
        wins = new int[numTeams];
        losses = new int[numTeams];
        gamesRemaining = new int[numTeams];
        teamNames = new String[numTeams];
        games = new int[numTeams][numTeams];
        nameIndexLookup = new ST<String, Integer>();
        // isEliminated = new boolean[numTeams];

        for (int i = 0; i < numTeams; i++) {
            String teamName = input.readString();
            nameIndexLookup.put(teamName, i);
            wins[i] = input.readInt();
            losses[i] = input.readInt();
            gamesRemaining[i] = input.readInt();
            teamNames[i] = teamName;
            // isEliminated[i] = false;
            for (int j = 0; j < numTeams; j++) {
                games[i][j] = input.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return nameIndexLookup.keys();
    }

    // number of wins for given team
    public int wins(String team) {
        enforceTeamExists(team);
        int index = nameIndexLookup.get(team);
        return wins[index];
    }

    // number of losses for given team
    public int losses(String team) {
        enforceTeamExists(team);
        int index = nameIndexLookup.get(team);
        return losses[index];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        enforceTeamExists(team);
        int index = nameIndexLookup.get(team);
        return gamesRemaining[index];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        enforceTeamExists(team1);
        enforceTeamExists(team2);
        int teamIndex1 = nameIndexLookup.get(team1);
        int teamIndex2 = nameIndexLookup.get(team2);
        return games[teamIndex1][teamIndex2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        enforceTeamExists(team);
        calculateElimination(team);
        return temporaryIsEliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        enforceTeamExists(team);
        // int index = nameIndexLookup.get(team);
        calculateElimination(team);
        if (!temporaryIsEliminated) {
            return null;
        }

        return temporaryCertificateOfElimination;
    }

    private void calculateElimination(String team) {
        temporaryIsEliminated = false;
        temporaryCertificateOfElimination = new SET<String>();
        calculateTrivialElimination(team);
        if (!temporaryIsEliminated) {
            // We only need to solve the MaxFlow if not trivially eliminated
            calculateMaxFlowElimination(team);
        }
    }

    private void calculateTrivialElimination(String team) {
        int potentialWins = wins(team) + remaining(team);
        for (int i = 0; i < numTeams; i++) {
            if (wins[i] > potentialWins) {
                // This is a team that has eliminated <team>
                temporaryIsEliminated = true;
                temporaryCertificateOfElimination.add(teamNames[i]);
            }
        }
    }

    private void calculateMaxFlowElimination(String team) {
        // The network has the following verticies, so calculate them:
        // - source and sink
        // - one for each game matchup
        // - one for each team
        int thisTeamIndex = nameIndexLookup.get(team);
        int numGameCombinations = 0;
        for (int i = 0; i < numTeams; i++) {
            for (int j = i + 1; j < numTeams; j++) {
                if (i != thisTeamIndex && j != thisTeamIndex && games[i][j] != 0) {
                    numGameCombinations++;
                }
            }
        }

        int numVertices = 2 + numGameCombinations + numTeams;
        int sourceVertex = numVertices - 2;
        int sinkVertex = numVertices - 1;
        FlowNetwork flow = new FlowNetwork(numVertices);

        // add team -> sink edges. Must do so they exist when adding game edges
        int thisTeamMaxWins = wins[thisTeamIndex] + gamesRemaining[thisTeamIndex];
        for (int i = 0; i < numTeams; i++) {
            // Don't worry about optimization - input size is always small
            if (i != thisTeamIndex) {
                int capacity = Math.max(thisTeamMaxWins - wins[i], 0);
                // int teamIndexInFlow = i + numTeams;
                FlowEdge teamToSink = new FlowEdge(i, sinkVertex, capacity);
                flow.addEdge(teamToSink);
            }
        }

        // add source -> game combination edges
        // track the ID of the vertex corresponding to this team
        int gameVertexEdge = numTeams;
        for (int i = 0; i < numTeams; i++) {
            for (int j = i + 1; j < numTeams; j++) {
                // Don't worry about optimizing - input size is always small
                if (i != thisTeamIndex && j != thisTeamIndex && games[i][j] != 0) {
                    // create an edge from source to game
                    FlowEdge gameEdge = new FlowEdge(sourceVertex, gameVertexEdge, games[i][j]);
                    // create an edge from game to team i
                    FlowEdge teamEdgeI = new FlowEdge(gameVertexEdge, i, Double.POSITIVE_INFINITY);
                    // create an edge from game to team j
                    FlowEdge teamEdgeJ = new FlowEdge(gameVertexEdge, j, Double.POSITIVE_INFINITY);

                    flow.addEdge(gameEdge);
                    flow.addEdge(teamEdgeI);
                    flow.addEdge(teamEdgeJ);

                    gameVertexEdge++;
                }
            }
        }

        // use the Ford Fulkerson algorithm to solve the Max Flow problem
        // we must check if all team -> sink vertexes are full. If not, the
        // certificate of elimination are the teams on the source-side of the
        // mincut
        FordFulkerson FF = new FordFulkerson(flow, sourceVertex, sinkVertex);
        for (int i = numTeams; i < numTeams + numGameCombinations; i++) {
            if (FF.inCut(i)) {
                for (FlowEdge e : flow.adj(i)) {
                    // Observe edges adjacent to i - only care about i -> sink
                    // want edges with sink as tail (the one being pointed to)
                    if (e.to() != i && !temporaryCertificateOfElimination
                            .contains(teamNames[e.to()])) {
                        temporaryCertificateOfElimination.add(teamNames[e.to()]);
                    }
                    /*
                    if (e.from() == sinkVertex && !temporaryCertificateOfElimination
                            .contains(teamNames[e.to()])) {
                        temporaryCertificateOfElimination.add(teamNames[e.to()]);
                    }*/
                }
            }
        }
        if (!temporaryCertificateOfElimination.isEmpty()) {
            temporaryIsEliminated = true;
        }
    }

    private void enforceTeamExists(String team) {
        if (!nameIndexLookup.contains(team)) {
            throw new java.lang.IllegalArgumentException("Invalid team name");
        }
    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
