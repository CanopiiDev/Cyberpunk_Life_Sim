// Het levensverhaal: alles wat er met je character is gebeurd (nieuwste bovenaan).
export default function LifeLog({ events }) {
  return (
    <div className="card log">
      {events.map((event) => (
        <p key={event.id}>
          <span className="log-age">{event.age} jr</span> {event.text}
        </p>
      ))}
    </div>
  );
}
