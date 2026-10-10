# Retrospective Log

One line per sprint: what worked, what did not, and one change for the next sprint.

Note: the repository was started on 5 Sep 2026 (the checkpoint window began on 27 Jul 2026), so the sprints below cover the work from 5 Sep to 10 Oct 2026.

| Sprint | Dates | Release | What worked | What did not work | One change for the next sprint |
|--------|-------|---------|-------------|-------------------|--------------------------------|
| 1 | 5 Sep - 8 Sep | v0.1.0, v0.2.0 | Project skeleton, login, product CRUD and admin panel were built quickly | I started late, so I had to work in fast, long sessions | Plan smaller tasks and commit more often |
| 2 | 9 Sep - 13 Sep | v1.0.0 | Security checklist, CI pipeline and live deploy on Render were finished | A privilege-escalation gap was found late and fixed at the end | Run the security checklist earlier, not at the end |
| 3 | 14 Sep - 20 Sep | v1.1.0 | Chatbot with a mock provider and a Gemini provider worked behind one interface | Gemini needed guardrails (rate limit, timeout, fallback) that took extra time | Write the guardrail tests first |
| 4 | 21 Sep - 4 Oct | v1.2.0, v1.3.0 | Order status workflow and seller dashboard were done with unit tests | Some pages needed polish after I tested them in the browser | Check the browser view earlier, before writing the tests |
| 5 | 5 Oct - 10 Oct | v1.4.0, v1.4.1, v1.5.0 | Wishlist, a bug fix with a test, and home page quick actions were verified on the live site | The server kept an old copy of the pages, so I had to rebuild and stop old Java processes before testing | After any change, run `mvn clean verify` and restart the server before checking the browser |