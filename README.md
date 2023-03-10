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

⚠ **Be careful not to lose your configs. Things _might_ go wrong.** ⚠

⚠ **You've been warned.** ⚠

⚠ **For real, make backups.** ⚠

## Available commands

| command                                  |
|------------------------------------------|
| /clientprofile                           |
| /clientprofile create _\<profile_name\>_ |
| /clientprofile delete _\<profile_name\>_ |
| /clientprofile list                      |
| /clientprofile switch _\<profile_name\>_ |
| /clientprofile save                      |


## License
See [LICENSE](LICENSE).
