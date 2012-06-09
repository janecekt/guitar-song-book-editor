<#-- @ftlvariable name="" type="com.songbook.core.model.SongNode" -->
<#-- @ftlvariable name="lineFragment" type="com.songbook.core.model.ChordNode" -->
<#-- @ftlvariable name="null" type="java.lang.String" -->
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf8">
        <!--suppress HtmlUnknownTarget -->
        <link href="../resources/song.css" rel="stylesheet" type="text/css" />
        <script src="../resources/song.js" type="text/javascript"></script>
        <title>${title?html}</title>
    </head>
    <body>
        <div class="title">${title?html}</div>

        <div class="transpose">
            Transposition: <span id="totaltranspose">0</span>
            [<a href="javascript:transpose(1)">+1</a>]
            [<a href="javascript:transpose(-1)">-1</a>]
        </div>

        <#list verseList as verseNode>
            <div class="verse">
                <#list verseNode.lineNodes as lineNode>
                    <#-- Create LINE (#rt = indent line by inserting everything left of <#rt>) -->
                    ${null!}<#rt>
                    <#list lineNode.contentList as lineFragment>
                        <#if lineFragment.type == "TextNode">
                            <#-- Simple text fragment (#t = ignore leading and trailing whitespace) -->
                            ${lineFragment.text?html}<#t>
                        <#elseif lineFragment.type == "SimpleChordNode">
                            <#-- Simple chord e.g. "C" (#t = ignore leading and trailing whitespace) -->
                            <span class="chord"><#t>
                                <span title="chord">${lineFragment.getChord1(0)?html}</span><#t>
                            </span><#t>
                        <#elseif lineFragment.type == "MultiChordNode">
                            <#-- Complex code e.g. "C/G" (#t = ignore leading and trailing whitespace) -->
                            <span class="chord">
                                <span title="chord">${lineFragment.getChord1(0)?html}</span><#t>
                                /<#t>
                                <span title="chord">${lineFragment.getChord2(0)?html}</span><#t>
                            </span>
                        </#if>
                    </#list>
                    <#-- Line break at the end of the line (#lt = ignore leading whitespace) -->
                    <br/><#lt>
                </#list>
            </div>
        </#list>
    </body>
</html>