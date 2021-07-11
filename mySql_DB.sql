create table menufeatures
(
    menu_id int                     not null,
    name    varchar(128) default '' not null,
    value   tinyint(1)   default 0  null
)
    charset = utf8;

create table menuitems
(
    id          int auto_increment
        primary key,
    menu_id     int      not null,
    section_id  int      null,
    description tinytext null,
    recipe_id   int      not null,
    position    int      null
)
    charset = utf8;

create table menus
(
    id        int auto_increment
        primary key,
    title     tinytext             null,
    owner_id  int                  null,
    published tinyint(1) default 0 null
)
    charset = utf8;

create table menusections
(
    id       int auto_increment
        primary key,
    menu_id  int      not null,
    name     tinytext null,
    position int      null
)
    charset = utf8;

create table preparations
(
    Id   int auto_increment
        primary key,
    name varchar(32) null
);

create table recipes
(
    id   int auto_increment
        primary key,
    name tinytext null
)
    charset = utf8;

create table roles
(
    id   char                            not null
        primary key,
    role varchar(128) default 'servizio' not null
)
    charset = utf8;

create table userroles
(
    user_id int              not null,
    role_id char default 's' not null
)
    charset = utf8;

create table users
(
    id       int auto_increment
        primary key,
    username varchar(128) default '' not null
)
    charset = utf8;

create table events
(
    id                    int          null,
    Id_chef               int          null,
    name                  varchar(128) null,
    date_start            date         null,
    date_end              date         null,
    expected_participants int          null,
    organizer_id          int          null,
    constraint events_users_id_fk
        foreign key (Id_chef) references users (id)
            on update cascade on delete cascade
);

create table summarysheets
(
    ID      int auto_increment
        primary key,
    Id_chef int not null,
    constraint summarysheets_users_id_fk
        foreign key (Id_chef) references users (id)
            on update cascade on delete cascade
);

create table services
(
    id                    int auto_increment
        primary key,
    event_id              int           not null,
    ID_sheet              int           null,
    name                  varchar(128)  null,
    proposed_menu_id      int default 0 not null,
    approved_menu_id      int default 0 null,
    service_date          date          null,
    time_start            time          null,
    time_end              time          null,
    expected_participants int           null,
    constraint services_summarysheets_ID_fk
        foreign key (ID_sheet) references summarysheets (ID)
            on update cascade on delete cascade
)
    charset = utf8;

create table summarysheet_preparations
(
    id_sheet       int not null,
    id_preparation int not null,
    primary key (id_preparation, id_sheet),
    constraint summarysheet_mixture_mixtures_Id_fk
        foreign key (id_preparation) references preparations (Id)
            on update cascade on delete cascade,
    constraint summarysheet_mixture_summarysheets_ID_fk
        foreign key (id_sheet) references summarysheets (ID)
            on update cascade on delete cascade
);

create table summarysheet_recipes
(
    id_sheet  int not null,
    id_recipe int not null,
    primary key (id_sheet, id_recipe),
    constraint summarysheet_recipes_recipes_id_fk
        foreign key (id_recipe) references recipes (id)
            on update cascade on delete cascade,
    constraint summarysheet_recipes_summarysheets_ID_fk
        foreign key (id_sheet) references summarysheets (ID)
            on update cascade on delete cascade
);

create table workshifts
(
    ID           int auto_increment
        primary key,
    `isKitchen?` tinyint(1) not null
);

create table cook_workshift
(
    Id_cook      int not null,
    Id_workshift int not null,
    primary key (Id_cook, Id_workshift),
    constraint chef_workshift_workshifts_ID_fk
        foreign key (Id_workshift) references workshifts (ID)
            on update cascade on delete cascade,
    constraint cook_workshift_users_id_fk
        foreign key (Id_cook) references users (id)
            on update cascade on delete cascade
);

create table tasks
(
    ID             int auto_increment
        primary key,
    ID_sheet       int                     not null,
    ID_cook        int                     null,
    ID_preparation int                     null,
    id_recipe      int                     null,
    ID_workshift   int                     null,
    time           int         default 1   not null,
    quantity       varchar(32) default '1' not null,
    portion        int         default 1   not null,
    `order`        int         default 0   null,
    constraint tasks_mixtures_Id_fk
        foreign key (ID_preparation) references preparations (Id)
            on update cascade on delete cascade,
    constraint tasks_recipes_id_fk
        foreign key (id_recipe) references recipes (id)
            on update cascade on delete cascade,
    constraint tasks_summarysheets_ID_fk
        foreign key (ID_sheet) references summarysheets (ID)
            on update cascade on delete cascade,
    constraint tasks_workshifts_ID_fk
        foreign key (ID_workshift) references workshifts (ID)
            on update cascade on delete cascade
)
    comment 'ID_cook da referenziare sulla tabella id del personale';


