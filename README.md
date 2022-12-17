# Client Profiles

## How it works
It simply copies the whole contents from the specified profile
directory into the main `/config/` folder and vice-versa.

If the current profile (e.g. the profile switching **FROM**) is
set to read-only, all files and directories (except those from
this mod) will be deleted. **_No way to recover them._**

However, if the profile is not set to read-only, all files and
directories currently present inside the `/config/` folder will
effectively overwrite (or _"save"_) that profile inside
`/config/clientprofiles/<profileName>/`.

Note that there's no designated `save` command _yet_, making
switching profiles the only way to save profiles _from within
the game_ currently.

⚠ **Be careful not to lose your configs. Things _might_ go wrong.** ⚠

⚠ **You've been warned.** ⚠

⚠ **For real, make backups.** ⚠

## Available commands

| command                                  |
|------------------------------------------|
| /configprofile                           |
| /configprofile create _\<profile_name\>_ |
| /configprofile delete _\<profile_name\>_ |
| /configprofile list                      |
| /configprofile switch _\<profile_name\>_ |


## License
See [LICENSE](LICENSE).
