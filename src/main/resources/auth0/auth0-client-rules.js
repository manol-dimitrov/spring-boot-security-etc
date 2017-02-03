/**
 * Created by dimitrovm on 07/10/2016.
 */

function spring_security_roles(user, context, callback) {
    user.app_metadata = user.app_metadata || {};
    var addRolesToUser = function (user, cb) {
        if (user.email.indexOf('@yahoo.com') > -1) {
            cb(null, ['ROLE_ARTIST']);
        } else {
            cb(null, ['ROLE_USER']);
        }
    };

    addRolesToUser(user, function (err, roles) {
        if (err) {
            callback(err);
        } else {
            user.app_metadata.roles = roles;
            auth0.users.updateAppMetadata(user.user_id, user.app_metadata)
                .then(function () {
                    callback(null, user, context);
                })
                .catch(function (err) {
                    callback(err);
                });
        }
    });
}