ALTER TABLE public.ticket
ADD COLUMN creation_date timestamp with time zone NOT NULL DEFAULT now();

ALTER TABLE public.ticket
ADD COLUMN last_modified_date timestamp with time zone;
