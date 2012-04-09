<#-- @ftlvariable name="bookId" type="java.lang.String" -->
<#-- @ftlvariable name="bookTitle" type="java.lang.String" -->
<#-- @ftlvariable name="bookCreator" type="java.lang.String" -->
<#-- @ftlvariable name="entryList" type="java.util.Collection<com.songbook.pc.util.EPubBuilder.Entry>" -->
<?xml version="1.0" encoding="UTF-8"?>
<package version="2.0" xmlns="http://www.idpf.org/2007/opf" unique-identifier="BookId">
    <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
        <dc:title>${bookTitle}</dc:title>
        <dc:creator>${bookCreator}</dc:creator>
        <dc:language>en-US</dc:language>
        <dc:rights>Public Domain</dc:rights>
        <dc:identifier id="BookId">urn:uuid:${bookId}</dc:identifier>
    </metadata>
    <manifest>
        <#-- All files in the EPUB document -->
        <#list entryList as entry>
            <item id="${entry.id}" href="${entry.fileName}" media-type="${entry.mime}"/>
        </#list>
    </manifest>
    <spine toc="toc.ncx">
        <#-- NCX is used to define reading order of the entries -->
        <#list entryList as entry>
            <#if entry.userReadable == true>
                <itemref idref="${entry.id}"/>
            </#if>
        </#list>
    </spine>
</package>