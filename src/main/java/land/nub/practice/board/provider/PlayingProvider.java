package land.nub.practice.board.provider;

import land.nub.practice.board.BoardProvider;
import land.nub.practice.game.duel.DuelState;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.player.Profile;
import land.nub.practice.util.PlayerUtils;
import land.nub.practice.util.TimeUtils;
import land.nub.practice.game.duel.Duel;
import land.nub.practice.game.duel.type.DuoDuel;
import land.nub.practice.game.duel.type.PartyDuel;
import land.nub.practice.game.duel.type.SoloDuel;
import land.nub.practice.game.party.Party;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayingProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        Profile profile = Profile.getByPlayer(player);
        Duel duel = profile.getCurrentDuel();
        switch(duel.getType()) {
            case ONE_VS_ONE: {
                SoloDuel soloDuel = (SoloDuel) duel;
                Player opponent = (soloDuel.getPlayerOne() == player ? soloDuel.getPlayerTwo() : soloDuel.getPlayerOne());
                if(duel.getState() == DuelState.PLAYING) {
                    lines.add("&6Opponent: &7" + opponent.getName());
                    lines.add("&6Duration: &7" + TimeUtils.msToMMSS(System.currentTimeMillis() - duel.getStartTime()));
                    lines.add("&6Ping: &7" + PlayerUtils.getPing(player) + " &f| &7" + PlayerUtils.getPing(opponent));
                } else if(duel.getState() == DuelState.STARTING) {
                    lines.add("&6Opponent: &7" + opponent.getName());

                    if(duel.getCountDownTask() != null) {
                        lines.add(" ");
                        lines.add("&6Starting: &7" + duel.getCountDown());
                    }
                } else if(duel.getState() == DuelState.ENDED) {
                    lines.add("&6Winner: &7" + soloDuel.getWinner().getName());
                }
                break;
            }
            case TWO_VS_TWO: {
                DuoDuel duoDuel = (DuoDuel) duel;
                if(duel.getState() == DuelState.PLAYING) {
                    List<Player> duo = duoDuel.getDuo(player);
                    Player teammate = duo.get(0).getName().equals(player.getName()) ? duo.get(1) : duo.get(0);
                    lines.add((duoDuel.getAlive().contains(teammate) ? "&a" : "&c") + teammate.getName());
                    if(duoDuel.getAlive().contains(teammate))
                        lines.add(PlayerUtils.getHealthColor(PlayerUtils.getHealth(teammate)).toString() + PlayerUtils.getHealth(teammate) + "\u2665 &7| &e" + PlayerUtils.getRemainingPots(teammate) + " pots");
                    else
                        lines.add("&c0.0\u2665 &7| &e0 pots");
                    lines.add(" ");

                    List<Player> otherDuo = duoDuel.getDuoOne() == duo ? duoDuel.getDuoTwo() : duoDuel.getDuoOne();
                    lines.add("&cOpponents: ");
                    for(Player opponent : otherDuo)
                        lines.add((duoDuel.getAlive().contains(opponent) ? "&f" : "&7&m") + opponent.getName());

                    lines.add(" ");
                    lines.add("&6Duration: &7" + TimeUtils.msToMMSS(System.currentTimeMillis() - duel.getStartTime()));
                } else if(duel.getState() == DuelState.STARTING) {
                    List<Player> otherDuo = duoDuel.getDuoOne() == duoDuel.getDuo(player) ? duoDuel.getDuoTwo() : duoDuel.getDuoOne();
                    lines.add("&cOpponents: ");
                    for(Player opponent : otherDuo)
                        lines.add((duoDuel.getAlive().contains(opponent) ? "&f" : "&7&m") + opponent.getName());

                    if(duel.getCountDownTask() != null) {
                        lines.add(" ");
                        lines.add("&6Starting: &7" + duel.getCountDown());
                    }
                } else if(duel.getState() == DuelState.ENDED) {
                    lines.add("&6Winner: &7" + PartyManager.getByUuid(duoDuel.getWinner()).getLeaderName() + "'s Party");
                }
                break;
            }
            case TEAM_VS_TEAM: {
                PartyDuel partyDuel = (PartyDuel) duel;
                if(duel.getState() == DuelState.PLAYING) {
                    Party party = partyDuel.getPartyOne().contains(player) ? partyDuel.getPartyOne() : partyDuel.getPartyTwo();
                    lines.add("&aTeam: &f" + partyDuel.getAlive(party) + "/" + party.getSize());

                    Party otherParty = partyDuel.getPartyOne() == party ? partyDuel.getPartyTwo() : partyDuel.getPartyOne();
                    lines.add("&cOpponents: &f" + partyDuel.getAlive(otherParty) + "/" + otherParty.getSize());

                    lines.add(" ");
                    lines.add("&6Duration: &7" + TimeUtils.msToMMSS(System.currentTimeMillis() - duel.getStartTime()));
                } else if(duel.getState() == DuelState.STARTING) {
                    Party party = partyDuel.getPartyOne().contains(player) ? partyDuel.getPartyOne() : partyDuel.getPartyTwo();
                    lines.add("&aTeam: &f" + partyDuel.getAlive(party) + "/" + party.getSize());

                    Party otherParty = partyDuel.getPartyOne() == party ? partyDuel.getPartyTwo() : partyDuel.getPartyOne();
                    lines.add("&cOpponents: &f" + partyDuel.getAlive(otherParty) + "/" + otherParty.getSize());

                    if(duel.getCountDownTask() != null) {
                        lines.add(" ");
                        lines.add("&6Starting: &7" + duel.getCountDown());
                    }
                } else if(duel.getState() == DuelState.ENDED) {
                    lines.add("&6Winner: &7" + partyDuel.getWinner().getLeaderName() + "'s Party");
                }
                break;
            }
            case FREE_FOR_ALL:
                break;
        }

        return lines;
    }
}
