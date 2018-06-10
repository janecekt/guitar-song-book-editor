{
    "type": "SongBook",
    <#if generatedOn??>"generatedOn": "${generatedOn?string("yyyy-MM-dd'T'HH:mm:ssZ")}",</#if>
    "songs": [
        <#list songNodes as songNode>
        {
            "type": "Song",
            "title": "${songNode.titleNode.title?trim}",
            <#if songNode.titleNode.subTitle??>"subTitle": "${songNode.titleNode.subTitle?trim}",</#if>
            <#if songNode.index??>"index": ${songNode.index},</#if>
            "verses": [
                <#list songNode.verseList as verseNode>
                {
                    "type": "Verse",
                    "lines": [
                        <#list verseNode.lineNodes as lineNode>
                        {
                            "type": "Line",
                            "fragments": [
                                <#list lineNode.contentList as lineFragment>
                                    <#if lineFragment.type == "TextNode">
                                        <#-- Simple text fragment (#t = ignore leading and trailing whitespace) -->
                                        {
                                            "type": "Text",
                                            "text": "${lineFragment.text.replace('\"', '\\\"')?trim}"
                                        }<#if lineFragment?has_next>,</#if>
                                    <#elseif lineFragment.type == "SimpleChordNode">
                                        <#-- Simple chord e.g. "C" (#t = ignore leading and trailing whitespace) -->
                                        {
                                            "type": "Chord",
                                            "chord1": "${lineFragment.getChord1(0)?trim}"
                                        }<#if lineFragment?has_next>,</#if>
                                    <#elseif lineFragment.type == "MultiChordNode">
                                        <#-- Complex code e.g. "C/G" (#t = ignore leading and trailing whitespace) -->
                                        {
                                            "type": "Chord",
                                            "chord1": "${lineFragment.getChord1(0)?trim}",
                                            "chord2": "${lineFragment.getChord2(0)?trim}"
                                        }<#if lineFragment?has_next>,</#if>
                                    </#if>
                                </#list>
                            ]
                        }<#if lineNode?has_next>,</#if>
                        </#list>
                    ]
                }<#if verseNode?has_next>,</#if>
                </#list>
            ]
        }<#if songNode?has_next>,</#if>
        </#list>
    ]
}