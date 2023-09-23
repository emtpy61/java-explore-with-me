DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS request CASCADE;
DROP TABLE IF EXISTS compilation CASCADE;
DROP TABLE IF EXISTS compilation_event CASCADE;



CREATE TABLE IF NOT EXISTS "person" (
  "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "email" VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "category" (
  "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "name" VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "location" (
  "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "lat" FLOAT NOT NULL,
  "lon" FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS "event" (
  "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "annotation" VARCHAR(2000),
  "category_id" BIGINT,
  "description" VARCHAR(7000),
  "event_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "created_on" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "initiator_id" BIGINT,
  "location_id" BIGINT,
  "paid" BOOLEAN,
  "participant_limit" INT,
  "published_on" TIMESTAMP WITHOUT TIME ZONE,
  "request_moderation" BOOLEAN,
  "state" VARCHAR(9),
  "title" VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS "request" (
  "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "created" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "event_id" BIGINT NOT NULL,
  "requester_id" BIGINT NOT NULL,
  "status" VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS "compilation" (
  "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "pinned" BOOLEAN,
  "title" VARCHAR(550) UNIQUE
);

CREATE TABLE IF NOT EXISTS "compilation_event" (
  "event_id" BIGINT,
  "compilation_id" BIGINT
);

ALTER TABLE "event" ADD CONSTRAINT "event_category_id_fk" FOREIGN KEY ("category_id") REFERENCES "category" ("id") ON DELETE CASCADE;

ALTER TABLE "event" ADD CONSTRAINT "event_initiator_id_fk" FOREIGN KEY ("initiator_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "event" ADD CONSTRAINT "event_location_id_fk" FOREIGN KEY ("location_id") REFERENCES "location" ("id") ON DELETE CASCADE;

ALTER TABLE "request" ADD CONSTRAINT "request_event_id_fk" FOREIGN KEY ("event_id") REFERENCES "event" ("id") ON DELETE CASCADE;

ALTER TABLE "request" ADD CONSTRAINT "request_requester_id_fk" FOREIGN KEY ("requester_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "compilation_event" ADD CONSTRAINT "compilation_m2m_event_id_fk" FOREIGN KEY ("event_id") REFERENCES "event" ("id");

ALTER TABLE "compilation_event" ADD CONSTRAINT "compilation_m2m_compilation_id_fk" FOREIGN KEY ("compilation_id") REFERENCES "compilation" ("id");
