package de.kuschku.quasseldroid_ng.ui.chat.chatview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.annotationbind.AutoString;
import de.kuschku.util.irc.IrcFormatHelper;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.ui.SpanFormatter;
import de.kuschku.quasseldroid_ng.ui.theme.ThemeUtil;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@UiThread
public class ChatMessageRenderer {
    @NonNull
    private final FormatStrings strings;

    private IrcFormatHelper helper;
    private MessageStyleContainer highlightStyle;
    private MessageStyleContainer serverStyle;
    private MessageStyleContainer actionStyle;
    private MessageStyleContainer plainStyle;

    @NonNull
    private AppContext context;

    public ChatMessageRenderer(@NonNull Context ctx, @NonNull AppContext context) {
        this.strings = new FormatStrings(ctx);
        this.context = context;
        setTheme(context.getThemeUtil());
    }

    public void setTheme(ThemeUtil themeUtil) {
        this.helper = new IrcFormatHelper(themeUtil.res);

        this.highlightStyle = new MessageStyleContainer(
                themeUtil.res.colorForegroundHighlight,
                Typeface.NORMAL,
                themeUtil.res.colorForegroundHighlight,
                themeUtil.res.colorBackgroundHighlight
        );
        this.serverStyle = new MessageStyleContainer(
                themeUtil.res.colorForegroundSecondary,
                Typeface.ITALIC,
                themeUtil.res.colorForegroundSecondary,
                themeUtil.res.colorBackgroundSecondary
        );
        this.plainStyle = new MessageStyleContainer(
                themeUtil.res.colorForeground,
                Typeface.NORMAL,
                themeUtil.res.colorForegroundSecondary,
                themeUtil.res.transparent
        );
        this.actionStyle = new MessageStyleContainer(
                themeUtil.res.colorForegroundAction,
                Typeface.ITALIC,
                themeUtil.res.colorForegroundSecondary,
                themeUtil.res.transparent
        );
    }

    private void applyStyle(@NonNull MessageViewHolder holder, @NonNull MessageStyleContainer style, @NonNull MessageStyleContainer highlightStyle, boolean highlight) {
        MessageStyleContainer container = highlight ? highlightStyle : style;
        holder.content.setTextColor(container.textColor);
        holder.content.setTypeface(null, container.fontstyle);
        holder.time.setTextColor(container.timeColor);
        holder.itemView.setBackgroundColor(container.bgColor);
    }

    @NonNull
    private CharSequence formatNick(@NonNull String hostmask, boolean full) {
        CharSequence formattedNick = helper.formatUserNick(IrcUserUtils.getNick(hostmask));
        if (full) {
            return strings.formatUsername(formattedNick, IrcUserUtils.getMask(hostmask));
        } else {
            return formattedNick;
        }
    }

    @NonNull
    private CharSequence formatNick(@NonNull String hostmask) {
        return formatNick(hostmask, context.getSettings().fullHostmask.or(false));
    }

    @NonNull
    private CharSequence getBufferName(Message message) {
        assertNotNull(context.getClient());
        Buffer buffer = context.getClient().getBuffer(message.bufferInfo.id);
        assertNotNull(buffer);
        String name = buffer.getName();
        assertNotNull(name);
        return name;
    }

    private void onBindPlain(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                strings.formatPlain(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    private void onBindNotice(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatAction(
                formatNick(message.sender, false),
                helper.formatIrcMessage(message.content)
        ));
    }

    private void onBindAction(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, actionStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                strings.formatAction(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    private void onBindNick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        if (message.flags.Self)
            holder.content.setText(strings.formatNick(
                    formatNick(message.sender, false)
            ));
        else
            holder.content.setText(strings.formatNick(
                    formatNick(message.sender, false),
                    helper.formatUserNick(message.content)
            ));
    }

    private void onBindMode(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindJoin(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatJoin(
                formatNick(message.sender),
                getBufferName(message)
        ));
    }

    private void onBindPart(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatPart(
                formatNick(message.sender),
                getBufferName(message),
                message.content
        ));
    }

    private void onBindQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatQuit(
                formatNick(message.sender),
                message.content
        ));
    }

    private void onBindKick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindKill(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindServer(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindInfo(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindError(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindDayChange(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatDayChange(
                context.getThemeUtil().formatter.getLongDateFormatter().print(message.time)
        ));
    }

    private void onBindTopic(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindNetsplitJoin(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindNetsplitQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindInvite(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBind(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.time.setText(context.getThemeUtil().formatter.getTimeFormatter().print(message.time));
        switch (message.type) {
            case Plain:
                onBindPlain(holder, message);
                break;
            case Notice:
                onBindNotice(holder, message);
                break;
            case Action:
                onBindAction(holder, message);
                break;
            case Nick:
                onBindNick(holder, message);
                break;
            case Mode:
                onBindMode(holder, message);
                break;
            case Join:
                onBindJoin(holder, message);
                break;
            case Part:
                onBindPart(holder, message);
                break;
            case Quit:
                onBindQuit(holder, message);
                break;
            case Kick:
                onBindKick(holder, message);
                break;
            case Kill:
                onBindKill(holder, message);
                break;
            case Server:
                onBindServer(holder, message);
                break;
            case Info:
                onBindInfo(holder, message);
                break;
            case Error:
                onBindError(holder, message);
                break;
            case DayChange:
                onBindDayChange(holder, message);
                break;
            case Topic:
                onBindTopic(holder, message);
                break;
            case NetsplitJoin:
                onBindNetsplitJoin(holder, message);
                break;
            case NetsplitQuit:
                onBindNetsplitQuit(holder, message);
                break;
            case Invite:
                onBindInvite(holder, message);
                break;
        }
    }

    private static class MessageStyleContainer {
        public final
        @ColorInt
        int textColor;
        public final int fontstyle;
        public final
        @ColorInt
        int timeColor;
        public final
        @ColorInt
        int bgColor;

        public MessageStyleContainer(int textColor, int fontstyle, int timeColor, int bgColor) {
            this.textColor = textColor;
            this.fontstyle = fontstyle;
            this.timeColor = timeColor;
            this.bgColor = bgColor;
        }
    }

    public static class FormatStrings {
        @AutoString(R.string.username_hostmask)
        public String username_hostmask;

        @AutoString(R.string.message_plain)
        public String message_plain;

        @AutoString(R.string.message_join)
        public String message_join;

        @AutoString(R.string.message_part)
        public String message_part;

        @AutoString(R.string.message_part_extra)
        public String message_part_extra;

        @AutoString(R.string.message_quit)
        public String message_quit;

        @AutoString(R.string.message_quit_extra)
        public String message_quit_extra;

        @AutoString(R.string.message_kill)
        public String message_kill;

        @AutoString(R.string.message_kick)
        public String message_kick;

        @AutoString(R.string.message_kick_extra)
        public String message_kick_extra;

        @AutoString(R.string.message_mode)
        public String message_mode;

        @AutoString(R.string.message_nick_self)
        public String message_nick_self;

        @AutoString(R.string.message_nick_other)
        public String message_nick_other;

        @AutoString(R.string.message_daychange)
        public String message_daychange;

        @AutoString(R.string.message_action)
        public String message_action;

        public FormatStrings(@NonNull Context ctx) {
            try {
                AutoBinder.bind(this, ctx);
            } catch (IllegalAccessException e) {
                Log.e("ERROR", e.toString());
                e.printStackTrace();
            }
        }

        @NonNull
        public CharSequence formatUsername(@NonNull CharSequence nick, @NonNull CharSequence hostmask) {
            return SpanFormatter.format(username_hostmask, nick, hostmask);
        }

        @NonNull
        public CharSequence formatJoin(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_join, user, channel);
        }

        @NonNull
        public CharSequence formatPart(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_part, user, channel);
        }

        @NonNull
        public CharSequence formatPart(@NonNull CharSequence user, @NonNull CharSequence channel, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatPart(user, channel);

            return SpanFormatter.format(message_part_extra, user, channel, reason);
        }

        @NonNull
        public CharSequence formatQuit(@NonNull CharSequence user) {
            return SpanFormatter.format(message_quit, user);
        }

        @NonNull
        public CharSequence formatQuit(@NonNull CharSequence user, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatQuit(user);

            return SpanFormatter.format(message_quit_extra, user, reason);
        }

        @NonNull
        public CharSequence formatKill(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_kill, user, channel);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked) {
            return SpanFormatter.format(message_kick, user, kicked);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatKick(user, kicked);

            return SpanFormatter.format(message_kick_extra, user, kicked, reason);
        }

        @NonNull
        public CharSequence formatMode(@NonNull CharSequence mode, @NonNull CharSequence user) {
            return SpanFormatter.format(message_mode, mode, user);
        }

        @NonNull
        public CharSequence formatNick(@NonNull CharSequence newNick) {
            return SpanFormatter.format(message_nick_self, newNick);
        }

        @NonNull
        public CharSequence formatNick(@NonNull CharSequence oldNick, @Nullable CharSequence newNick) {
            if (newNick == null || newNick.length() == 0) return formatNick(oldNick);

            return SpanFormatter.format(message_nick_other, oldNick, newNick);
        }

        @NonNull
        public CharSequence formatDayChange(@NonNull CharSequence day) {
            return SpanFormatter.format(message_daychange, day);
        }

        @NonNull
        public CharSequence formatAction(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_action, user, channel);
        }

        @NonNull
        public CharSequence formatPlain(@NonNull CharSequence nick, @NonNull CharSequence message) {
            return SpanFormatter.format(message_plain, nick, message);
        }
    }
}
