var express = require('express');
var createError = require('http-errors');
var database = require('../utils/database');
var { createDataFromArray, createDataFromString } = require('../utils/dataProcessors');

var router = express.Router();

<#list content as entity>
<#if entity.hasOptions>
const default${entity.name} = {
    <#list entity.values as options>
    "${options}": false,
    </#list>
}
</#if>
</#list>

/* GET all listing. */
router.get('/', (req, res, next) => {
    const searchString = req.query.s;
    database.query('SELECT * FROM entity', (err, rows) => {
        if (err) return next(createError(500, err.message));
        if (rows.length === 0) return res.redirect('/entity/insert');
        if (!searchString) return res.render('getAll', {
            title: 'All Data',
            entity: rows,
            keys: Object.keys(rows[0])
        });

        const filtered = rows.filter(eachData =>
            (Object.values(eachData).map(eachData => eachData.toString().includes(searchString)))
                .includes(true)
        );
        return res.render('getAll', {
            title: 'Search Result',
            entity: filtered,
            queryString: searchString,
            searchMessage: "Showing results of '" + searchString + "'.",
            keys: Object.keys(rows[0])
        });
    });
});

/* GET insert form. */
router.get('/insert', (_, res) => {
    return res.render('form', {
        title: 'Insert Data',
        <#list content as entity>
        <#if entity.hasOptions>
        ${entity.name}: default${entity.name},
        </#if>
        </#list>
    });
});

/* GET single listing. */
router.get('/:id', (req, res, next) => {
    const entityId = req.params.id;
    database.query("SELECT * FROM entity WHERE id=" + entityId, (err, rows) => {
        if (err) next(createError(500, err.message));
        if (rows.length === 0) return next(createError(404, "Entity id " + entityId + " is not found."));
        <#list content as entity>
        <#if entity.isCheckbox==1>
        const ${entity.name} = createDataFromArray(default${entity.name}, rows[0].${entity.name});
        </#if>
        <#if entity.type=="radio">
        const ${entity.name} = createDataFromString(default${entity.name}, rows[0].${entity.name});
        </#if>
        <#if entity.type=="select">
        const ${entity.name} = createDataFromString(default${entity.name}, rows[0].${entity.name});
        </#if>
        </#list>
        return res.render('form', {
            title: 'Data Details',
            ...rows[0],
            <#list content as entity>
            <#if entity.hasOptions>
            ${entity.name},
            </#if>
            </#list>
        });
    });
});

/* POST create or update listing. */
router.post('/insert', (req, res, next) => {
    const data = req.body;
    // check if checkbox only have one selected data
    <#list content as entity>
    <#if entity.isCheckbox==1>
    if (!(data.${entity.name} instanceof Array)) data.${entity.name} = [data.${entity.name}];
    </#if>
    </#list>
    if (data.type === 'CREATE') {
        database.query(`INSERT INTO entity(
        <#list content as entity>
        ${entity.name}<#if entity?has_next>,</#if>
        </#list>
        ) VALUES ?`,
            [[[
                <#list content as entity>
                <#if entity.isCheckbox==1>
                JSON.stringify(data.${entity.name}),
                <#else>
                data.${entity.name},
                </#if>
                </#list>
            ]]],
            err => {
                if (err) return next(createError(500, err.message));
                return res.redirect('/entity');
            });
    } else {
        database.query(`UPDATE entity SET
            <#list content as entity>
            ${entity.name} = ?<#if entity?has_next>,</#if>
            </#list>
            WHERE id=${'$\{data.id}'}`,
            [
                <#list content as entity>
                <#if entity.isCheckbox==1>
                JSON.stringify(data.${entity.name}),
                <#else>
                data.${entity.name},
                </#if>
                </#list>
            ],
            err => {
                if (err) return next(createError(500, err.message));
                return res.redirect('/entity');
            });
    }
});

/* DELETE single listing. */
router.delete('/:id', (req, res, next) => {
    const entityId = req.params.id;
    database.query(`DELETE FROM entity WHERE id=${'$\{entityId}'}`, (err, result) => {
        if (err) next(createError(500, err.message));
        return res.send('Affected rows = ' + result.affectedRows);
    });
});

module.exports = router;
