# nosql
A bit of experimentation on how can we safely audit every single mongo requests without logging entire query( which may contain user sensitivie data)

Relies on facade pattern to create a custom mongo template in order to meet audit requirements
