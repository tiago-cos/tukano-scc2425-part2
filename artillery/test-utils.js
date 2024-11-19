let storedUsers = [];
let storedShorts = [];

function generateUrlSafeString(length) {
    const urlSafeChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_';
    let result = '';

    for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * urlSafeChars.length);
        result += urlSafeChars[randomIndex];
    }

    return result;
}

function uploadRandomizedUser(requestParams, context, ee, next) {
    const username = generateUrlSafeString(16);
    const password = "password";
    const email = `${username}@campus.fct.unl.pt`;
    const displayName = generateUrlSafeString(16);

    const user = {
        userId: username,
        pwd: password,
        email: email,
        displayName: displayName
    };
    requestParams.body = JSON.stringify(user);
    return next();
}

function requestUploadedUser(requestParams, context, ee, next) {
    if (storedUsers.length === 0) {
        return next(new Error('No users in the store yet'));
    }

    const user = storedUsers[Math.floor(Math.random() * storedUsers.length)];
    requestParams.url = requestParams.url.replace("/users/{userId}", `/users/${user}?pwd=password`);

    return next();
}

function storeUser(requestParams, response, context, ee, next) {
    if (typeof response.body !== 'undefined' && response.body.length > 0) {
        storedUsers.push(response.body);
    }
    return next();
}

function uploadRandomizedShort(requestParams, context, ee, next) {
    if (storedUsers.length === 0) {
        return next(new Error('No users in the store yet'));
    }

    const user = storedUsers[Math.floor(Math.random() * storedUsers.length)];
    requestParams.url = requestParams.url.replace("/shorts/{userId}", `/shorts/${user}?pwd=password`);

    return next();
}

function storeShort(requestParams, response, context, ee, next) {
    if (typeof response.body !== 'undefined' && response.body.length > 0) {
        storedShorts.push(response.body);
    }
    return next();
}

function requestUploadedShort(requestParams, context, ee, next) {
    if (storedShorts.length === 0) {
        return next(new Error('No shorts in the store yet'));
    }

    const short = storedShorts[Math.floor(Math.random() * storedShorts.length)];
    const shortId = JSON.parse(short).shortId;
    requestParams.url = requestParams.url.replace("/shorts/{shortId}", `/shorts/${shortId}`);

    return next();
}

module.exports = {
    uploadRandomizedUser,
    storeUser,
    requestUploadedUser,
    uploadRandomizedShort,
    storeShort,
    requestUploadedShort
};