import * as _ from "lodash";

export interface KeyValue<K,V> {
    key: K,
    value: V
}

export function toLookupMap<I,K>(input : I[], keyMapper : (it : I) => K) : Map<K,I> {
    return toMap(input, keyMapper, it => it);
}

export function toMap<I,K,V>(input : I[], keyMapper : (it : I) => K, valueMapper: (it : I) => V) : Map<K,V> {
    let map = new Map<K,V>();
    input.forEach(it => { map.set(keyMapper(it), valueMapper(it))});
    return map;
}

export function toMultiMap<I,K,V>(input : I[], keyMapper : (it : I) => K, valueMapper: (it : I) => V) : Map<K,V[]> {
    let map = new Map<K,V[]>();
    input.forEach(it => {
            const key = keyMapper(it);
            let mapValue = map.get(key);
            if (mapValue === undefined) {
                map.set(key, [valueMapper(it)]);
            } else {
                mapValue.push(valueMapper(it));
            }
        });
    return map;
}

export function toKeyValueList<K,V>(map : Map<K,V>) : KeyValue<K,V>[] {
    let result : KeyValue<K,V>[] = [];
    map.forEach((value, key) => {
        result.push({key: key, value: value});
    });
    return result;
}

export function groupBy<I,K,V>(input : I[], keyMapper : (it : I) => K, valueMapper: (it : I) => V) : KeyValue<K,V[]>[] {
    const map = toMultiMap(input, keyMapper, valueMapper);
    return toKeyValueList(map);
}

export function sortBy<I,K>(input : I[], keyMapper : (it : I) => K, reverse : boolean) : I[] {
    return (reverse === true)
        ? _.chain(input).sortBy(keyMapper).reverse().value()
        : _.chain(input).sortBy(keyMapper).value();
}

export function toDateOnlyString(date : Date) : string {
    return date.getFullYear()
        + "-"
        + padNumber(date.getMonth()+1, 2)
        + "-"
        + padNumber(date.getDate(), 2);
}

export function toDateStringWithoutYear(date : Date, alt : string) : string {
    return _.isNil(date)
        ? alt
        : date.getDate() + "/" + (date.getMonth()+1)
}

export function toDateStringWithYear(date : Date, alt : string) : string {
    return _.isNil(date)
        ? alt
        : date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()
}

export function addDays(date : Date, daysToAdd : number) : Date {
    if (_.isNil(date)) {
        return null;
    }
    return daysToAdd === 0
        ? date
        : new Date(date.getFullYear(), date.getMonth(), date.getDate() + daysToAdd, date.getHours(), date.getMinutes(), date.getSeconds(), date.getMilliseconds());
}

export function getStartOfWeek(date : Date) : Date {
    if (_.isNil(date)) {
        return null;
    }
    return getStartOfDay(addDays(date, -date.getDay()));
}

export function getEndOfWeek(date : Date) : Date {
    if (_.isNil(date)) {
        return null;
    }
    return getEndOfDay(addDays(date, 7-date.getDay()));
}

export function getStartOfDay(date : Date) : Date {
    if (_.isNil(date)) {
        return null;
    }
    return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0,0,0,0);
}

export function getEndOfDay(date : Date) : Date {
    if (_.isNil(date)) {
        return null;
    }
    return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
}


export function toDateWithoutTime(date : Date) : Date {
    return _.isNil(date)
        ? null
        : new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0, 0);
}


export function toTimeString(date : Date, alt : string) : string {
    return _.isNil(date)
        ? alt
        : date.getHours() + ":" + padNumber(date.getMinutes(), 2);
}

const daysOfWeek : string[] = ['Neděle', 'Pondělí', 'Úterý', 'Středa', 'Čtvrtek', 'Pátek', 'Sobota'];

export function toDayOfWeek(date : Date, alt: string) : string {
    return _.isNil(date)
        ? alt
        : daysOfWeek[date.getDay()];
}

export function lookupMap<K,V>(key : K, map : Map<K,V>) : V {
    if (_.isNil(key) || _.isNil(map)) {
        return null;
    }
    const value = map.get(key);

    return _.isNil(value)
        ? null
        : value;
}

export function padNumber(num : number, size : number) : string {
    return ('000000000000' + num).substr(-size);
}

// Memoize accent replacement (unicode implementation is slow)
const noAccentMap = toLookupMap(
    ["á", "č", "ď", "é", "ě", "í", "ň", "ó", "ř", "š", "ť", "ú", "ů", "ý", "ž",
        "Á", "Č", "Ď", "É", "Ě", "Í", "Ň", "Ó", "Ř", "Š", "Ť", "Ú", "Ů", "Ý", "Ž"],
    str => removeAccentsUnicode(str)
);

export function toStringWithoutAccents(str : string) : string {
    let out = '';
    for (let i=0; i<str.length; i++) {
        let result = noAccentMap.get(str[i]);
        out += _.isNil(result) ? str[i] : result;
    }
    return out;
}

export function removeAccentsUnicode(str : string) : string {
    return str.normalize('NFD').replace(/[\u0300-\u036f]/g, "")
}

// Remove accents, remove whitespaces, lowercase
export function toSearchString(str : string) : string {
    if (_.isNil(str)) {
        return "";
    }
    return removeAccentsUnicode(str)
        .toLowerCase()
        .replace('/[^a-z]/', "");
}

export function toFirstCapitalLetter(str : string) : string {
    if (_.isNil(str)) {
        return str;
    }

    return str.split(" ")
        .map(name => name.substr(0, 1).toUpperCase() + name.substr(1).toLowerCase() )
        .join(" ");
}

// Immutable updates
export function updateObject<T>(source : T, patch : Partial<T>) : T {
    let obj = Object.assign({}, source, patch);
    for (let prop in obj) {
        if (obj.hasOwnProperty(prop)) {
            if (obj[prop] === null) {
                delete obj[prop];
            }
        }
    }
    return obj;
}

export function updateArray<T,I>(array : T[], newItem : T, matcher : (item1 : T) => boolean) : T[] {
    let wasMatched = false;
    let result = array.map(item => {
        if (matcher(item)) {
            wasMatched = true;
            return newItem;
        } else {
            return item;
        }
    });
    if (!wasMatched) {
        result.push(newItem);
    }
    return result;
}

export function coalesce<T>(...items: T[]) : T {
    if (!_.isNil(items)) {
        const result = items.find(item => !_.isNil(item));
        if (!_.isNil(result)) {
            return result;
        }
    }
    return null;
}