<#-- @ftlvariable name="" type="com.songbook.core.model.SongNode" -->
<#-- @ftlvariable name="lineFragment" type="com.songbook.core.model.ChordNode" -->
<#-- @ftlvariable name="null" type="java.lang.String" -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${title}</title>
        <link href="epub-stylesheet.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="title">${title}</div>

        <#list verseList as verseNode>
            <div class="verse">
            <#list verseNode.lineNodes as lineNode>
                <#-- Create LINE (#rt = indent line by inserting everything left of <#rt>) -->
                ${null!}<#rt>
                <#list lineNode.contentList as lineFragment>
                    <#if lineFragment.type == "TextNode">
                        <#-- Simple text fragment (#t = ignore leading and trailing whitespace) -->
                        ${lineFragment.text}<#t>
                    <#elseif lineFragment.type == "SimpleChordNode">
                        <#-- Simple chord e.g. "C" (#t = ignore leading and trailing whitespace) -->
                        <span class="chord">${lineFragment.getChord1(0)}</span><#t>
                    <#elseif lineFragment.type == "MultiChordNode">
                        <#-- Complex code e.g. "C/G" (#t = ignore leading and trailing whitespace) -->
                        <span class="chord">${lineFragment.getChord1(0)}/${lineFragment.getChord2(0)}</span><#t>
                    </#if>
                </#list>
                <#-- Line break at the end of the line (#lt = ignore leading whitespace) -->
                <br/><#lt>
            </#list>
            </div>

        </#list>
    </body>
</html>
